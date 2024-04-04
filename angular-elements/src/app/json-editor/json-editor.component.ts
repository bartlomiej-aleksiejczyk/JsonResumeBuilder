import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  Component,
  ElementRef,
  HostListener,
  ViewChild,
  Input,
  Attribute,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import * as monaco from 'monaco-editor';
import { JsonEditorService } from '../json-editor-service.service';

@Component({
  selector: 'app-json-editor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './json-editor.component.html',
  styleUrls: ['./json-editor.component.scss'],
})
export class JsonEditorComponent implements AfterViewInit {
  @Input() url: string = ''; // Default empty string
  @Input() jsonData: string = '{}'; // Default empty object as string
  @Input() isReadonly: string = 'false'; // Default to false

  errorMessage: string = '';
  editor: monaco.editor.IStandaloneCodeEditor | undefined;
  isResizing: boolean = false;
  successMessage: string = '';
  protected _isReadonly: boolean = false;
  private _parsedJsonData: any; // Use any or a specific interface

  constructor(private jsonEditorService: JsonEditorService) {}

  @ViewChild('editorContainer') editorContainer!: ElementRef;
  @ViewChild('editorWrapper') editorWrapper!: ElementRef;

  ngAfterViewInit(): void {
    this._isReadonly = this.isReadonly === 'true';
    console.log(this._isReadonly);
    try {
      this._parsedJsonData = JSON.parse(this.jsonData);
    } catch (error) {
      console.error('Error parsing jsonData input:', error);
      this._parsedJsonData = {}; // Keep it as an object here
    }
    this.initMonaco();
  }

  private initMonaco(): void {
    this.editor = monaco.editor.create(this.editorContainer.nativeElement, {
      value: JSON.stringify(this._parsedJsonData, null, 2),
      readOnly: this._isReadonly, // Use the parsed boolean value
      automaticLayout: true,
    });
  }

  startResize(event: MouseEvent): void {
    this.isResizing = true;
  }

  @HostListener('document:mousemove', ['$event'])
  onMouseMove(event: MouseEvent) {
    if (!this.isResizing) {
      return;
    }
    const editorWrapperRect =
      this.editorWrapper.nativeElement.getBoundingClientRect();
    const newHeight = event.clientY - editorWrapperRect.top;

    const minHeight = 100;
    this.editorWrapper.nativeElement.style.height = `${Math.max(
      newHeight,
      minHeight
    )}px`;
    this.editor?.layout();
  }

  @HostListener('document:mouseup', ['$event'])
  onMouseUp(event: MouseEvent) {
    if (this.isResizing) {
      this.isResizing = false;
    }
  }

  validateJson(): void {
    if (this.editor) {
      try {
        const json = JSON.parse(this.editor.getValue());
        this.jsonData = json;
        this.errorMessage = '';
        this.successMessage = 'JSON is valid';
      } catch (e) {
        this.errorMessage = 'Invalid JSON';
        this.successMessage = '';
      }
    }
  }
  sendJson(): void {
    if (this.editor && this.url) {
      try {
        const json = JSON.parse(this.editor.getValue());
        this.jsonEditorService.sendJson(json, this.url).subscribe({
          next: (response: unknown) => {
            console.log('JSON sent successfully', response);
            this.successMessage = 'JSON sent successfully';
            this.errorMessage = '';
          },
          error: (error: unknown) => {
            console.error('Failed to send JSON', error);
            this.errorMessage = 'Failed to send JSON';
            this.successMessage = '';
          },
        });
      } catch (e) {
        this.errorMessage = 'Invalid JSON';
        this.successMessage = '';
      }
    } else {
      this.errorMessage = 'URL not provided';
      this.successMessage = '';
    }
  }
}

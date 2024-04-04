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
  @Input() url: string = '';
  @Input() jsonData: string = '{}';
  @Input() isReadonly: string = 'false';

  errorMessage: string = '';
  successMessage: string = '';
  protected _isReadonly: boolean = false;
  private _parsedJsonData: any;

  constructor(private jsonEditorService: JsonEditorService) {}

  @ViewChild('jsonInput') jsonInput!: ElementRef;
  @ViewChild('lineNumbers') lineNumbers!: ElementRef<HTMLElement>;

  ngAfterViewInit(): void {
    this._isReadonly = this.isReadonly === 'true';
    console.log(this._isReadonly);
    try {
      this._parsedJsonData = JSON.parse(this.jsonData);
    } catch (error) {
      console.error('Error parsing jsonData input:', error);
      this._parsedJsonData = {};
    }
    this.updateLineNumbers();
    this.jsonInput.nativeElement.addEventListener('scroll', () => {
      this.syncScroll();
    });
  }

  validateJson(): void {
    const input = this.jsonInput.nativeElement.value;
    try {
      JSON.parse(input);
      this.successMessage = 'Valid JSON';
      this.errorMessage = '';
    } catch (e) {
      this.successMessage = '';
      this.errorMessage = 'Invalid JSON';
    }
  }

  sendJson(): void {
    if (this.jsonInput && this.url) {
      try {
        const json = JSON.parse(this.jsonInput.nativeElement.value);
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
  updateLineNumbers(): void {
    const lines = this.jsonInput.nativeElement.value.split('\n').length;
    this.lineNumbers.nativeElement.innerText =
      Array.from({ length: lines }, (v, k) => k + 1).join('\n') + '\n#';
  }

  syncScroll(): void {
    if (this.lineNumbers && this.jsonInput) {
      const scrollTop = this.jsonInput.nativeElement.scrollTop;
      this.lineNumbers.nativeElement.scrollTop = scrollTop;
    }
  }

  handleKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      event.preventDefault();

      const textarea = this.jsonInput.nativeElement;
      const value = textarea.value;
      const selectionStart = textarea.selectionStart;

      let indent = '';
      let newLineIndent = '';
      let postCursorText = '';
      let decreaseIndent = false;

      const lines = value.substring(0, selectionStart).split('\n');
      if (lines.length > 0) {
        const lastLine = lines[lines.length - 1];
        indent = lastLine.match(/^\s*/)[0];
      }

      const precedingChar = value.substring(0, selectionStart).trim().slice(-1);
      if (['{', '['].includes(precedingChar)) {
        newLineIndent = indent + '  ';
      } else if (['}', ']'].includes(precedingChar)) {
        if (indent.length >= 2) {
          newLineIndent = indent.substring(0, indent.length - 2);
          decreaseIndent = true;
        } else {
          newLineIndent = '';
        }
      } else {
        newLineIndent = indent;
      }

      if (selectionStart < value.length) {
        postCursorText = value.substring(textarea.selectionEnd);
      }

      let insertText = '\n' + newLineIndent;
      if (decreaseIndent && ['}', ']'].includes(precedingChar)) {
        insertText = '\n' + newLineIndent + '\n' + indent;
      }

      textarea.value =
        value.substring(0, selectionStart) + insertText + postCursorText;
      let newPosition = selectionStart + newLineIndent.length + 1;
      if (decreaseIndent && ['}', ']'].includes(precedingChar)) {
        newPosition += newLineIndent.length + 1;
      }

      textarea.selectionStart = textarea.selectionEnd = newPosition;

      this.updateLineNumbers();
    }
  }
}

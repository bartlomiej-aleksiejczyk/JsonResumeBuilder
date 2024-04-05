import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-json-renderer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './json-renderer.component.html',
  styleUrls: ['./json-renderer.component.scss'],
})
export class JsonRendererComponent implements OnInit {
  @Input() jsonData: string = '{}';
  formattedJson: string = '';
  highlightedJson: string = '';

  ngOnInit(): void {
    this.highlightJson();
  }

  ngOnChanges(): void {}

  renderJson(): void {
    try {
      const obj = JSON.parse(this.jsonData);
      this.formattedJson = JSON.stringify(obj, null, 2);
    } catch (error) {
      this.formattedJson = 'Invalid JSON';
    }
  }

  highlightJson(): void {
    try {
      const obj = JSON.parse(this.jsonData);
      const json = JSON.stringify(obj, null, 2);
      this.highlightedJson = this.syntaxHighlight(json);
    } catch (error) {
      this.highlightedJson = '<span class="error">Invalid JSON</span>';
    }
  }

  syntaxHighlight(json: string): string {
    json = json
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;');
    return json.replace(
      /("(\\u[\da-fA-F]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d+)?(?:[e-]\d+)?)/g,
      (match) => {
        let cls = 'number';
        if (/^"/.test(match)) {
          if (/:$/.test(match)) {
            cls = 'key';
          } else {
            cls = 'string';
          }
        } else if (/true|false/.test(match)) {
          cls = 'boolean';
        } else if (/null/.test(match)) {
          cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
      }
    );
  }
}

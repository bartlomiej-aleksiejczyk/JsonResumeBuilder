import { Component, Injector } from '@angular/core';
import { createCustomElement } from '@angular/elements';
import { JsonEditorComponent } from './json-editor/json-editor.component';
import { JsonEditorService } from './json-editor-service.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [JsonEditorComponent], // Assuming you use RouterOutlet somewhere else or remove it if not used
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [JsonEditorService] // Ensure the service is provided
})
export class AppComponent {
  title = 'json-editor-displayer';

  constructor(injector: Injector) {
    if (!customElements.get('json-editor-element')) {
      const el = createCustomElement(JsonEditorComponent, { injector });
      customElements.define('json-editor-element', el);
    }
  }
}

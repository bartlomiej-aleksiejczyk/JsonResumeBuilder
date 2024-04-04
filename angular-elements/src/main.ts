import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { createCustomElement } from '@angular/elements';
import { createApplication } from '@angular/platform-browser';
import { JsonEditorComponent } from './app/json-editor/json-editor.component';

// bootstrapApplication(AppComponent, appConfig).catch((err) =>
//   console.error(err)
// );

/**
 * Below code makes standalone webcomponent
 * To use it comment bootstrapApplication and uncomment codde below
 */

(async () => {
  const app = createApplication(appConfig);

  const customCardElement = createCustomElement(JsonEditorComponent, {
    injector: (await app).injector,
  });

  customElements.define('json-editor-element', customCardElement);
})();

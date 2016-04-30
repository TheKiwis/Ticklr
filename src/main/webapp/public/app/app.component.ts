/**
 * @author ngnmhieu
 * @since 21.04.16
 */

import {Component} from "angular2/core";
import {LoginComponent} from "./login.component";
import {HTTP_PROVIDERS} from "angular2/http";

@Component({
    selector: "app",
    templateUrl: "app/templates/app.component.html",
    directives: [LoginComponent],
    providers: [HTTP_PROVIDERS]
})
export class AppComponent {
}
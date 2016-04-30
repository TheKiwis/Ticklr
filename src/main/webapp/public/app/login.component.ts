import {Component, OnInit, Input} from "angular2/core";
import {AuthService} from "./auth.service";
import {JwtHelper} from "angular2-jwt/angular2-jwt";

/**
 * @author ngnmhieu
 * @since 29.04.16
 */
@Component({
    selector: 'login',
    templateUrl: 'app/templates/login.component.html',
    providers: [AuthService, JwtHelper]
})
export class LoginComponent implements OnInit {

    private _isVisible: boolean;

    @Input("email") email: string;
    @Input("password") password: string;

    constructor(private _auth: AuthService) {
    }

    ngOnInit() {
        this._isVisible = false;
    }

    login() {
        this._auth.authenticate(this.email, this.password)
            .subscribe(
                user => console.log(user),
                error => {
                    // errors
                }
            );
    }

    show() {
        this._isVisible = true;
    }

    close() {
        this._isVisible = false;
    }

    isVisible() {
        return this._isVisible;
    }
}

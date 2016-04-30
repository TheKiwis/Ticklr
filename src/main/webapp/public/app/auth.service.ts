import {Injectable} from "angular2/core";
import {Http, Headers, RequestOptions, Response} from "angular2/http";
import {Observable} from "rxjs/Observable";
import {User} from "./user";
import {JwtHelper} from 'angular2-jwt/angular2-jwt';

/**
 * @author ngnmhieu
 * @since 29.04.16
 */
@Injectable()
export class AuthService {

    private _user:User;

    private USER_URL = "/api/users/request-auth-token";

    constructor(private _http:Http, private _jwtHelper:JwtHelper) {
    }

    /**
     * @returns if user is authenticated
     */
    isAuthenticated():boolean {
        return this._user != null;
    }

    /**
     * Performs user authentication
     * @returns true if authentication succeeds, else false
     */
    authenticate(email:string, password:string):Observable<any> {

        let headers = new Headers({'Content-Type': 'application/json'});
        let options = new RequestOptions({headers: headers});
        let body = {email: email, password: password};

        return this._http.post(this.USER_URL, JSON.stringify(body), options)
            .map((res:Response) => {

                if (res.status == 200) {
                    let jwtToken = this._jwtHelper.decodeToken(res.json().key);
                    this._user = new User(jwtToken.sub, new Date(jwtToken.exp * 1000));
                    return this._user;
                }

                throw new Error("Authentication failed: " + res.status);

            }).catch(error => {

                let errMsg = error.message || 'Unknown error';
                console.log(errMsg);
                return Observable.throw(errMsg);
            });
    }

    /**
     * @returns authenticated user
     *          null if user is not authenticated
     */
    get user():User {
        return null;
    }
}
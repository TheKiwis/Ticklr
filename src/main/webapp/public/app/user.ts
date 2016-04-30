/**
 * @author ngnmhieu
 * @since 29.04.16
 */

export class User {
    id:string;
    expiration:Date;

    constructor(id:string, expiration:Date) {
        this.id = id;
        this.expiration = expiration;
    }
}
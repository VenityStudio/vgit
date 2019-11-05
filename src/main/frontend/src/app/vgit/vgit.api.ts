import {$SCRIPTOPTIONS} from "../../script.options";
import {$VGIT$, VgitService} from "./vgit.service";
import {VGitUserAPI} from "./api/VGitUserAPI";
import {FormGroup} from "@angular/forms";
import {HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";

export class VGitAPI {

  user: VGitUserAPI;

  constructor(private vgit: VgitService) {
    this.bindDynamic("user", () => {  /*--*/
      return new VGitUserAPI();                     /*--*/ // TODO: FIX IT
    });                                             /*--*/
  }


  bindDynamic(prop, creator) {

    Object.defineProperty(this, prop, {
      get(): any {
        if (this["_" + prop] === undefined) {
          this["_" + prop] = creator();
        }
        return this["_" + prop];
      }
    });
  }


  buildHeaders(headers) {

    return new HttpHeaders(headers);

  }

  buildParams(params) {
    let sParam = "";
    for (const pKey in params) {
      const pVal = params[pKey];
      sParam += "&" + encodeURIComponent(pKey) + "=" + encodeURIComponent(pVal);
    }
    return sParam.substr(1)

  }

  method(method, params, callback, httpMethod: string = "GET", customHeaders = {}, customOptions = {}) {

    const url = $SCRIPTOPTIONS.BASE_URL + $SCRIPTOPTIONS.api.path + method;
    let req: Observable<Object> = undefined;
    let options = {
      headers: this.buildHeaders(customHeaders),
      ...customOptions
    };

    if (httpMethod.toLocaleLowerCase() == "post") {

      req = this.vgit.http.post(url, params, options);

    } else {
      req = this.vgit.http.get(url + "?" + this.buildParams(params), options)
    }

    req.subscribe(
      response => {
        callback({
          ok: true,
          ...response
        })
      },
      error => {
        console.log(error);
        callback({
          ok: false
        })
      }
    )

  }

  buildModule(path: string) {
    return new VGitAPIModule(path, this);
  }
}


export class VGitAPIModule {


  constructor(protected path: string, protected vgit: VGitAPI) {
  }


  callMethod<cb extends Function>(sub: string | null, params: any, callback: cb, method: string = "GET") {
    this.vgit.method((this.path ? this.path + "/" : "/") + (sub ? sub : ""), params, (res) => {
      if (res instanceof HttpErrorResponse) {
        callback(
          {
            error: new VGitResponseErrorWrapper(res.message, res.status, res),
            ok: false
          }
        )
      } else {

      }
    }, method);
  }

  callReactive<cb extends Function>(sub: string | null, params: FormGroup, callback: cb, method: string = "GET") {
    this.vgit.method((this.path ? this.path + "/" : "/") + (sub ? sub : ""), params.value, callback, method);
  }
}


export interface VGitError {
  message: string
  code: number
}

export class VGitResponseErrorWrapper implements VGitError {
  message: string;
  code: number;
  original: HttpErrorResponse;

  constructor(message, code, original) {
    this.message = message;
    this.code = code;
    this.original = original;
  }
}

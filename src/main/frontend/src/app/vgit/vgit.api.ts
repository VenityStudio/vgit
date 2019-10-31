import {$SCRIPTOPTIONS} from "../../script.options";
import {$VGIT$, VgitService} from "./vgit.service";
import {VGitUserAPI} from "./api/VGitUserAPI";
import {FormGroup} from "@angular/forms";



export class VGitAPI {

  user: VGitUserAPI;

  constructor(private vgit: VgitService) {
    this.bindDynamic("user", () => {  /*--*/
      return new VGitUserAPI();                     /*--*/ // TODO: FIX IT
    });                                             /*--*/
  }


  bindDynamic(prop, creator){

    Object.defineProperty(this, prop, {
      get(): any {
        if (this["_" + prop] === undefined){
          this["_" + prop] = creator();
        }
        return this["_" + prop];
      }
    });
  }



  buildUrl(method, params) {
    let sParam = "";
    for (const pKey in params) {
      const pVal = params[pKey];
      sParam += "&" + encodeURIComponent(pKey) + "=" + encodeURIComponent(pVal);
    }
    return $SCRIPTOPTIONS.BASE_URL + $SCRIPTOPTIONS.api.path + method + "?" + sParam.substr(1)

  }

  method(method, params, callback) {
    this.vgit.http.get(this.buildUrl(method, params))
      .subscribe(resp => {
        callback(resp, true);
      }, error => {
        callback(error, false);
      });
  }

  buildModule(path: string) {
    return new VGitAPIModule(path, this);
  }
}


export class VGitAPIModule {


  constructor(protected path: string, protected vgit: VGitAPI) {
  }


  callMethod<cb extends Function>(sub: string | null, params: any, callback: cb) {
    this.vgit.method((this.path ? this.path + "/" : "/") + (sub ? sub : ""), params, callback);
  }
  callReactive<cb extends Function>(sub: string | null, params: FormGroup, callback: cb) {
    this.vgit.method((this.path ? this.path + "/" : "/") + (sub ? sub : ""), params.value, callback);
  }
}

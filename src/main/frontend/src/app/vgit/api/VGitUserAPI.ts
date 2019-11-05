import {VGitAPIModule} from "../vgit.api";
import {$VGIT$} from "../vgit.service";

export class VGitUserAPI {
  module: VGitAPIModule = $VGIT$.api.buildModule("user");


  register(login: string, email: string, fullName: string, password: string, callback) {
    this.module.callMethod("register", {login, email, fullName, password}, callback, "POST");
  }
}


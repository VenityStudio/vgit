import { Injectable } from '@angular/core';
import {VGitLang} from "./vgit.lang";
import {VGitUI} from "./vgit.ui";
import {VGitAuthProvider} from "./vgit.auth.provider";

@Injectable({
  providedIn: 'root'
})
export class VgitService {

  lang: VGitLang;
  ui: VGitUI;
  auth: VGitAuthProvider;

  constructor() {
    $VGIT$ = this;
    this.ui = new VGitUI();
    this.lang = new VGitLang();
    this.auth = new VGitAuthProvider();
  }


}

export default function $$(key) {
  if ($VGIT$){
    const keys = key.split(".");
    let temp = $VGIT$.lang.current;
    for (const keyItem of keys){
      temp = temp[keyItem];
    }
    return temp;
  }
  return "[VGit is not loaded]";
}

export let $VGIT$: VgitService = null;

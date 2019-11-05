import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {VGitComponent} from "../../vgit/vgit.component";
import {VgitService} from "../../vgit/vgit.service";

@Component({
  selector: 'app-auth-module',
  templateUrl: './auth-module.component.html',
  styleUrls: ['./auth-module.component.less']
})
export class AuthModuleComponent extends VGitComponent implements OnInit {

  login = new FormGroup({
    login: new FormControl(''),
    password: new FormControl(''),
  });

  registration = new FormGroup({
    login: new FormControl(''),
    password: new FormControl(''),
    repeatPassword: new FormControl(''),
    fullName: new FormControl(''),
    email: new FormControl(''),
  });

  constructor(private formGroupBuilder: FormBuilder, private vgit: VgitService) {
    super();
  }

  LOGIN(){
  }

  REGISTER(){
    const {login, email, fullName, password, repeatPassword} = this.registration.value
    if (password !== repeatPassword) {
      return;
    }
    this.vgit.api.user.register(login, email, fullName, password, response => {
    })
  }

  ngOnInit() {
  }

}

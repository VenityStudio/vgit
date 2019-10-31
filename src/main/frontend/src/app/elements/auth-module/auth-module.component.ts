import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {VGitComponent} from "../../vgit/vgit.component";

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

  constructor(private formGroupBuilder: FormBuilder) {
    super();
  }

  LOGIN(){
    console.log(this.login)
  }

  REGISTER(){
    console.log(this.registration)
  }

  ngOnInit() {
  }

}

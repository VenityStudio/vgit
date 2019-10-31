import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-auth-module',
  templateUrl: './auth-module.component.html',
  styleUrls: ['./auth-module.component.less']
})
export class AuthModuleComponent implements OnInit {

  login = new FormGroup({
    login: new FormControl(''),
    password: new FormControl(''),
  });

  constructor(private formGroupBuilder: FormBuilder) {
  }

  LOGIN(){
    console.log(this.login)
  }

  ngOnInit() {
  }

}

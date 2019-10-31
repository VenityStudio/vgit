import {VGitEnUsLang} from './langs/en_US.lang';
import {VGitRuRuLang} from './langs/ru_RU.lang';

export class VGitLang {
  langs: Map<string, Lang> = new Map<string, Lang>();
  current: Lang = new VGitRuRuLang();

  constructor() {
    this.langs.set('ru-ru', new VGitRuRuLang());
    this.langs.set('en-us', new VGitEnUsLang());
  }
}

export interface Lang {
  name: string;
  menu: LangMenuOptions;
  pages: LangPagesOptions;
  authProvider: AuthProviderOptions;
}

export interface LangMenuOptions {
  main: string;
  browse: string;
}

export interface LangMainOptions {
  noAuthTitle: string,
  noAuthCardTitle: string,
  noAuthCardContent: string
}

export interface AuthProviderOptions {
  loginTab: string
  registrationTab: string
  login: LangMainLoginOptions
  registration: LangMainRegistrationOptions
}

export interface LangMainLoginOptions {
  login: string;
  password: string;
  button: string;
}
export interface LangMainRegistrationOptions {
  login: string;
  password: string;
  fullName: string,
  email: string,
  repeatPassword: string,
  button: string;
}




export interface LangPairOptions {
  title: string;
  sub: string;
}

export interface LangPagesOptions {
  main: LangMainOptions;
}

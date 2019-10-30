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
}

export interface LangMenuOptions {
  main: string;
  browse: string;
}

export interface LangMainOptions {
  title: string;
  subtitle: string;
  register: string;
  login: string;
  reasons: LangMainReasonsOptions;
}

export interface LangMainReasonsOptions {
  together: LangPairOptions;
  share: LangPairOptions;
  management: LangPairOptions;
}

export interface LangPairOptions {
  title: string;
  sub: string;
}

export interface LangPagesOptions {
  main: LangMainOptions;
}

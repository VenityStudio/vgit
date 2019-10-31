import {Lang} from '../vgit.lang';

export class VGitEnUsLang implements Lang {
  name = 'English (US)';
  menu = {
    main: 'Main',
    browse: 'Browse'
  };
  authProvider: {

    registrationTab: "Sing up",
    loginTab: "Sign in",
    login: {
      login: "Login",
      password: "Password",
      button: "Sign in"
    },
    registration: {
      login: "Login",
      password: "Password",
      repeatPassword: "Repeat password",
      email: "Email",
      fullName: "Full name",
      button: "Sing up"
    }

  };

  pages = {
    main: {
      noAuthTitle: "Welcome to VGit",
      noAuthCardTitle: "Hello there!",
      noAuthCardContent: "Please login to see this page"
    }
  };
}

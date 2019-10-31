import {Lang} from '../vgit.lang';

export class VGitRuRuLang implements Lang {
  name = 'Русский (Россия)';
  menu = {
    main: 'Главная',
    browse: 'Проекты'
  };
  authProvider = {
    registrationTab: "Регистрация",
    loginTab: "Вход",
    login: {
      login: "Логин",
      password: "Пароль",
      button: "Войти"
    },
    registration: {
      login: "Логин",
      password: "Пароль",
      repeatPassword: "Повторите пароль",
      email: "Эл. Почта",
      fullName: "Имя Фамилия",
      button: "Зарегестрироваться"
    }

  };
  pages = {
    main: {
      noAuthTitle: "Добро пожаловать в VGit",
      noAuthCardTitle: "Приветствуем вас",
      noAuthCardContent: "Пожалуйста, войдите для продолжения"
    }
  };
}

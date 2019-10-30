import {Lang} from '../vgit.lang';

export class VGitRuRuLang implements Lang {
  name = 'Русский (Россия)';
  menu = {
    main: 'Главная',
    browse: 'Проекты'
  };
  pages = {
    main: {
      title: 'Добро пожаловать в VGit',
      subtitle: 'Пожалуйста, авторизуйтесь для продолжения',
      register: 'Регистрация',
      login: 'Вход',
      reasons: {
        together: {
          title: 'Вместе',
          sub: 'Разрабатывайте код вместе'
        },
        share: {
          title: 'Делитесь',
          sub: 'Делитесь кодом с другими на бегу'
        },
        management: {
          title: 'Управляйте',
          sub: 'Управляйте проектами и следите за ними'
        }
      }
    }
  };
}

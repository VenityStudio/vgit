import {Lang} from '../vgit.lang';

export class VGitEnUsLang implements Lang {
  name = 'English (US)';
  menu = {
    main: 'Main',
    browse: 'Browse'
  };
  pages = {
    main: {
      title: 'Welcome to VGit',
      subtitle: 'Please, authorize for continue',
      register: 'Registration',
      login: 'Login',
      reasons: {
        together: {
          title: 'Together',
          sub: 'Develop together'
        },
        share: {
          title: 'Share',
          sub: 'Share code with others on fly'
        },
        management: {
          title: 'Manage',
          sub: 'Manage project and view for they activity'
        }
      }
    }
  };
}

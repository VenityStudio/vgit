export class VGitUI {

  active = 'main';
  menuItems: MenuItem[] = [
    {
      text: 'menu.main',
      id: 'main',
      route: '/'
    },
    {
      text: 'menu.browse',
      id: 'browse',
      route: '/browse'
    },
  ];

}

export interface MenuItem {
  text: string;
  route?: string;
  id: string;
}


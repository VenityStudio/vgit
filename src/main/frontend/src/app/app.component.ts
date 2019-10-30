import {Component} from '@angular/core';
import {VgitService} from './vgit/vgit.service';
import {VGitComponent} from './vgit/vgit.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.less']
})
export class AppComponent extends VGitComponent {
  drawerOpened: boolean;
  constructor(private vgit: VgitService) {
    super();
    this.drawerOpened = true;
  }
}


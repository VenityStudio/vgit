import { Component, OnInit } from '@angular/core';
import {VgitService} from "../../vgit/vgit.service";
import {VGitComponent} from "../../vgit/vgit.component";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent extends VGitComponent implements OnInit {

  constructor(private vgit: VgitService) {
    super();
  }

  ngOnInit() {
  }

}

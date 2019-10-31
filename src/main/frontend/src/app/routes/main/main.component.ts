import { Component, OnInit } from '@angular/core';
import {VgitService} from "../../vgit/vgit.service";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

  constructor(private vgit: VgitService) { }

  ngOnInit() {
  }

}

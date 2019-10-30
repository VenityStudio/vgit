import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {MainComponent} from "./routes/main/main.component";
import {NotFoundComponent} from "./routes/not-found/not-found.component";


const routes: Routes = [
  { path: '',
    component: MainComponent
  },
  { path: 'browse',
    component: MainComponent
  },
  { path: '**',
    component: NotFoundComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

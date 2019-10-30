export class VGitAuthProvider {
  isLoggedIn: boolean;
  token?: string;

  constructor() {
    this.isLoggedIn = false;
    this.token = null;
  }

}

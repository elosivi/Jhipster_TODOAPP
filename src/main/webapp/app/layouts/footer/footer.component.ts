import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { LoginService } from '../../login/login.service';
import { TranslateService } from '@ngx-translate/core';
import { StateStorageService } from '../../core/auth/state-storage.service';
import { AccountService } from '../../core/auth/account.service';
import { ProfileService } from '../profiles/profile.service';
import { VERSION } from '../../app.constants';

@Component({
  standalone: true,
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
  imports: [RouterModule],
})
export default class FooterComponent {
  constructor(private router: Router) {}
}

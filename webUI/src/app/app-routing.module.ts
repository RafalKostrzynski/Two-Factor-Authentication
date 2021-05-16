import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AfterRegistrationComponent } from './components/after-registration/after-registration.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { HomeComponent } from './components/home/home.component';
import { RegisterComponent } from './components/register/register.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { SecondFactorComponent } from './components/second-factor/second-factor.component';
import { SettingsComponent } from './components/settings/settings.component';
import { SignInComponent } from './components/sign-in/sign-in.component';
import { VerifyEmailComponent } from './components/verify-email/verify-email.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'sign-in', component: SignInComponent },
  { path: 'second-factor', component: SecondFactorComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'after-registration', component: AfterRegistrationComponent },
  { path: 'verify-email/:token', component: VerifyEmailComponent},
  { path: 'reset-password/:token', component: ResetPasswordComponent},
  { path: 'settings', component: SettingsComponent},
  { path: '', redirectTo: '/sign-in', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

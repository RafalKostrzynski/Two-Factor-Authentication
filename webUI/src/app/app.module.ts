import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from "@angular/material/button";
import { MatInputModule } from "@angular/material/input";
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';
import {MatCardModule} from '@angular/material/card';
import {MatSelectModule} from '@angular/material/select';
import { ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { HeaderComponent } from './components/header/header.component';
import { RegisterComponent } from './components/register/register.component';
import { SignInComponent } from './components/sign-in/sign-in.component';
import { HomeComponent } from './components/home/home.component';
import { FlexModule } from '@angular/flex-layout';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { AfterRegistrationComponent } from './components/after-registration/after-registration.component';
import { VerifyEmailComponent } from './components/verify-email/verify-email.component';
import { QRCodeModule } from 'angularx-qrcode';
import { SecondFactorComponent } from './components/second-factor/second-factor.component';
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { TokenInterceptorService } from './services/token-interceptor.service';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { HeaderAuthenticatedComponent } from './components/header-authenticated/header-authenticated.component';
import { SettingsComponent } from './components/settings/settings.component';
import { MatTabsModule } from "@angular/material/tabs";
import { SettingsUserComponent } from './components/settings-user/settings-user.component';
import { SettingsKeyComponent } from './components/settings-key/settings-key.component';



@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    RegisterComponent,
    SignInComponent,
    HomeComponent,
    ForgotPasswordComponent,
    AfterRegistrationComponent,
    VerifyEmailComponent,
    SecondFactorComponent,
    ResetPasswordComponent,
    HeaderAuthenticatedComponent,
    SettingsComponent,
    SettingsUserComponent,
    SettingsKeyComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatButtonModule,
    HttpClientModule,
    MatInputModule,
    ReactiveFormsModule,
    MatToolbarModule,
    MatIconModule,
    MatListModule,
    FlexModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    QRCodeModule,
    MatTabsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptorService, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

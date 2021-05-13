import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { User } from 'src/app/models/user';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss']
})
export class SignInComponent implements OnInit {
  @ViewChild('fform') signInFormDirective: any;

  signInForm = new FormGroup({
      username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(30)]),
      password: new FormControl('', [Validators.required, Validators.minLength(9), Validators.maxLength(60), Validators.pattern("((?=.*\d)|(?=.*\W+))(?![.\n])(?=.*[A-Z])(?=.*[a-z]).*$")]),
  })
  user!: User;

  validationMessages = {
    'username': {
      'required': 'Username is required.',
      'minlength': 'Username must be at least 5 characters long.',
      'maxlength': 'Username can´t be longer than 30 characters.',
    },
    'password': {
      'required': 'Password is required.',
      'minlength': 'Password must be at least 9 characters long.',
      'maxlength': 'Password can´t be longer than 60 characters.',
      'pattern': 'Password must contain at least 1 uppercase, 1 special characters and 1 digit'
    }
  };

  getErrorMessage() {
    if (this.signInForm.errors) {
      return 'You must enter a value';
    }

    return this.email.hasError('email') ? 'Not a valid email' : '';
  }
  getUsernameErrorMessage(){
    
  }

  getPasswordErrorMessage(){

  }
  constructor() { }

  ngOnInit(): void {
  }

  onSubmit(){

  }

}

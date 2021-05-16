import { ValidatorFn, AbstractControl, ValidationErrors } from "@angular/forms";

export const validatePasswords: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
    const password = group.get('password')?.value;
    var repeatPasswordControl = group.get('repeatPassword');
    const confirmPassword = repeatPasswordControl?.value;
    var same = (password === confirmPassword);
  
    if (!same) {
      repeatPasswordControl?.setErrors({
        inValidPassword: true
      })
    } else repeatPasswordControl?.setErrors(null);
  
    return (!same) ? {
      inValidPassword: true
    } : null;
  }
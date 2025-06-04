$(function () {

    // User Register Validation
    var $userRegister=$("#userRegister");

    $userRegister.validate({
        rules:{
            name:{
                required:true,
                lettersonly:true,
            },
            email:{
                required:true,
                space:true,
                email:true,
            },
            mobileNumber:{
                required:true,
                space:true,
                numericOnly:true,
                minlength:10,
                maxlength:12,
            },
            password:{
                required:true,
                space:true,
            },
            cPassword:{
                required:true,
                space:true,
                equalTo:"#password",
            },
            address:{
                required:true,
                all:true,
            },
            city:{
                required:true,
            },
            state:{
                required:true,
            },
            pincode:{
                required:true,
                numericOnly:true,
                space:true,
            },
        },
        messages:{
            name:{
                required:'Name Required!',
                lettersonly:'Invalid Name!'
            },
            email:{
                required:'Email Required!',
                space:'Space Not Allowed!',
                email:'Invalid Email!'
            },
            mobileNumber:{
                required:'Mobile Number Required!',
                space:'Space Not Allowed!',
                numericOnly:'Invalid Mobile Number!',
                minlength:'10 digits minimum!',
                maxlength:'12 digits maximum'
            },
            password:{
                required:'Password Required!',
                space:'Space Not Allowed!',
            },
            cPassword:{
                required:'Confirm Password Required!',
                space:'Space Not Allowed!',
                equalTo:'Password Not Match!'
            },
            address:{
                required:'Address Required!',
                all:'Invalid Address!',
            },
            city:{
                required:'City Required!',
            },
            state:{
                required:'State Required!',
            },
            pincode:{
                required:'Pincode Required!',
                numericOnly:'Invalid Pincode!',
                space:'Space Not Allowed!',
            },
        }
    })

    // Reset Password Validation

    var $resetPasswordForm=$("#resetPasswordForm");

    $resetPasswordForm.validate({
        rules:{
            password:{
                required:true,
                space:true,
            },
            cPassword:{
                required:true,
                space:true,
                equalTo:"#resetPassword",
            }
        },
        messages:{
            password:{
                required:'Password Required!',
                space:'Space Not Allowed!',
            },
            cPassword:{
                required:'Confirm Password Required!',
                space:'Space Not Allowed!',
                equalTo:'Password Not Match!'
            }
        }
    })
})


jQuery.validator.addMethod("lettersonly", function(value, element) {
    return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
})

jQuery.validator.addMethod("space", function(value, element) {
    return /^[^-\s]+$/.test(value);
})

jQuery.validator.addMethod("all", function(value, element) {
    return /^[^-\s][a-zA-Z0-9_,.\s-]+$/.test(value);
})

jQuery.validator.addMethod("numericOnly", function(value, element) {
    return /^[0-9]+$/.test(value);
})
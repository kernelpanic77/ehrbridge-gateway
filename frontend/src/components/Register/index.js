import React, { useState } from 'react';
import Swal from 'sweetalert2';
import axios from 'axios';
const Register = ({ setIsAuthenticated, setIsRegistering }) => {
  // const adminEmail = 'admin@example.com';
  // const adminPassword = 'qwerty';

  const [fname, setFname] = useState('');
  const [lname, setLname] = useState('');
  const [phoneNum, setPhoneNum] = useState('');
  const [dob, setDob] = useState('');
  const [address, setAddress] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [otp, setOtp] = useState();
  const [isOtpVerified, setIsOtpVerified] = useState(false);
  const [isOtpRecieved, setIsOtpRecieved] = useState(false);
  

  const handleVerifyOTP = e => {
    e.preventDefault();
    

      const config = {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      };

      const bodyParameters = {
      otp: otp
      };

    axios.post("http://localhost:8080/api/v1/auth/verifyOtp",bodyParameters,config).then((response) => {
      console.log(response);
      if (response.data != null) {
        // store auth token in local storage
        Swal.fire({
          timer: 1500,
          showConfirmButton: false,
          willOpen: () => {
            Swal.showLoading();
          },
          willClose: () => {
            localStorage.setItem('token', response.data.token)
            localStorage.setItem('is_authenticated', true);
            localStorage.setItem('abha_id', response.data.ehrbid);
            setIsAuthenticated(true);
  
            Swal.fire({
              icon: 'success',
              title: 'OTP Verified',
              showConfirmButton: false,
              timer: 1500,
            });
          },
        });
      } else {
        Swal.fire({
          timer: 1500,
          showConfirmButton: false,
          willOpen: () => {
            Swal.showLoading();
          },
          willClose: () => {
            Swal.fire({
              icon: 'error',
              title: 'Error!',
              text: 'Incorrect OTP',
              showConfirmButton: true,
            });
          },
        });
      }
    });
   
  }

  const handleRegister = e => {
    e.preventDefault();
    let details = {
      firstName : fname,
      lastName : lname,
      phoneString : phoneNum,
      password:password,
      address : address,
      emailAddress : email, 
      gender: "M"
    }
    // send a post request with details to http://localhost:8090/author/Register
    axios.post("http://localhost:8080/api/v1/auth/register/user",details).then((response) => {
      console.log(response);
      if (response.data != null) {
        // store auth token in local storage
        Swal.fire({
          timer: 1500,
          showConfirmButton: false,
          willOpen: () => {
            Swal.showLoading();
          },
          willClose: () => {
            localStorage.setItem('token', response.data.token)
            setIsOtpRecieved(true);
            Swal.fire({
              icon: 'success',
              title: 'OTP Sent to Email',
              showConfirmButton: false,
              timer: 1500,
            });
          },
        });
      } else {
        Swal.fire({
          timer: 1500,
          showConfirmButton: false,
          willOpen: () => {
            Swal.showLoading();
          },
          willClose: () => {
            Swal.fire({
              icon: 'error',
              title: 'Error!',
              text: 'Error',
              showConfirmButton: true,
            });
          },
        });
      }
    });

  };

  return (
    <div className="small-container">
      <form onSubmit={handleRegister}>
        <h1>Patient ABHA ID Register</h1>
        <div style={{display:"flex", justifyContent:"space-between"}}>
        <input
          id="fname"
          type="text"
          name="fname"
          placeholder="First Name"
          value={fname} 
          onChange={e => setFname(e.target.value)}
          style={{marginRight:"20px"}}
        />
        <input
          id="lname"
          type="text"
          name="lname"
          placeholder="Last Name"
          value={lname} 
          onChange={e => setLname(e.target.value)}
        />
        </div>
        <div style={{display:"flex", justifyContent:"space-between"}}>
        <input
          id="phoneNum"
          type="text"
          name="phoneNum"
          placeholder="Phone Number"
          value={phoneNum} 
          onChange={e => setPhoneNum(e.target.value)}
          style={{marginRight:"20px"}}
        />
      <input
          id="email"
          type="email"
          name="email"
          placeholder="Email Address"
          value={email}
          onChange={e => setEmail(e.target.value)}
        />
        </div>
        <label htmlFor="dob">Date of Birth</label>
        <input
          id="dob"
          type="date"
          name="dob"
          placeholder="Date of Birth"
          value={dob} 
          onChange={e => setDob(e.target.value)}
        />
        <label htmlFor='address'>Patient Address</label>
        <input
          id="address"
          type="text"
          name="address"
          placeholder="Address"
          value={address} 
          onChange={e => setAddress(e.target.value)}
          style={{marginRight:"20px"}}
        />
                <label htmlFor='password'>Password</label>
        <input
          id="address"
          type="password"
          name="password"
          placeholder="Password"
          value={password} 
          onChange={e => setPassword(e.target.value)}
          style={{marginRight:"20px"}}
        />
        {isOtpRecieved && (
                <div style={{display:"flex", justifyContent:"space-between"}}>
        <input
          id="phoneNum"
          type="number"
          name="phoneNum"
          placeholder="Enter OTP"
          value={otp} 
          onChange={e => setOtp(e.target.value)}
          style={{marginRight:"20px"}}
        />
          <input
            className="button"
            type="button"
            value="Verify OTP"
            onClick={handleVerifyOTP}
          />          
        </div>)}
        <input style={{ marginTop: '12px' }} type="submit" value="Register" />
      </form>
      <label for = "login">Already have an account?</label>
      <input
            className="button"
            type="button"
            value="Login"
            onClick={() => setIsRegistering(false)}
          />
    </div>
  );
};

export default Register;

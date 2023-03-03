import React, { useState } from 'react';
import Swal from 'sweetalert2';
import axios from 'axios';
const Login = ({ setIsAuthenticated, setIsRegistering }) => {
  // const adminEmail = 'admin@example.com';
  // const adminPassword = 'qwerty';

  const [abhaID, setAbhaID] = useState('');
  const [phoneNum, setPhoneNum] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [otp, setOtp] = useState();
  const [isOtpRecieved, setIsOtpRecieved] = useState(false);
  const [isOtpVerified, setIsOtpVerified] = useState(false);
   const handleVerifyOTP = e => {
    e.preventDefault();
    

      const config = {
        headers: { Authorization: `Bearer ${JSON.parse(localStorage.getItem('token'))}` }
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
            localStorage.setItem('abha_id', response.data.abha_id);
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

  const handleLogin = e => {
    e.preventDefault();
    let details = {
      emailAddress : email, 
      password:"password",
    }
    // send a post request with details to http://localhost:8090/author/Register
    axios.post("http://localhost:8080/api/v1/auth/signin/user",details).then((response) => {
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
      <form onSubmit={handleLogin} >
        <h1 style={{textAlign:"center"}}>Patient Login</h1>
        <label htmlFor="email">Email Address</label>
        <input
          id="email"
          type="email"
          name="email"
          placeholder="Email Address"
          value={email}
          onChange={e => setEmail(e.target.value)}
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
                        <div style={{display:"flex", justifyContent:"space-between", marginTop:"50px"}}>
        <input
          id="otp"
          type="number"
          name="otp"
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
        <input style={{ marginTop: '12px' }} type="submit" value="Login" />
      </form>
      <label for = "register">Don't have an account?</label>
      <input
            className="button"
            type="button"
            value="Register"
            onClick={() => setIsRegistering(true)}
          />
    </div>
  );
};

export default Login;

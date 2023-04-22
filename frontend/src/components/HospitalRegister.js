import React, { useState } from 'react';
import Swal from 'sweetalert2';
import axios from 'axios';
const HospitalRegister = () => {
  // const adminEmail = 'admin@example.com';
  // const adminPassword = 'qwerty';
/*
    "hospitalName": "hiu",
    "emailAddress": "hiu@email.com",
    "phoneString": "1234677890",
    "address": "Delhi",
    "hospitalLicense": "kjgjdskjdjkld",
    "hook_url": "http://localhost:8081"
*/
  const [hospitalName, setHospitalName] = useState('');
  const [emailAddress, setEmailAddress] = useState('');
  const [phoneString, setPhoneString] = useState('');
  const [address, setAddress] = useState('');
  const [hospitalLicense, setHospitalLicense] = useState('');
  const [hook_url, setHook_url] = useState('');


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
      hospitalName: hospitalName,
      emailAddress: emailAddress,
      phoneString: phoneString,
      address: address,
      hospitalLicense: hospitalLicense,
      hook_url: hook_url
    }
    // send a post request with details to http://localhost:8090/author/Register
    axios.post("http://localhost:8080/api/v1/auth/register/hospital",details).then((response) => {
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
            localStorage.setItem('doctor_ehrb_id', response.data.hospitalId);
            Swal.fire({
              icon: 'success',
              title: "Success, Your ABHA ID: " + response.data.hospitalId + "\n Your API Key: " + response.data.api_key,
              showConfirmButton: true,
              willClose: () => {
                window.location.reload();
              }
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
        <h1>Hospital ABHA ID Register</h1>
        <label htmlFor='hospitalName'>Hospital Name</label>
        <input
          id="hospitalName"
          type="text"
          name="hospitalName"
          placeholder="Hospital Name"
          value={hospitalName} 
          onChange={e => setHospitalName(e.target.value)}
          style={{marginRight:"20px"}}
        />
        <div style={{display:"flex", justifyContent:"space-between"}}>
        <input
          id="phoneString"
          type="text"
          name="phoneString"
          placeholder="Phone Number"
          value={phoneString} 
          onChange={e => setPhoneString(e.target.value)}
          style={{marginRight:"20px"}}
        />
      <input
          id="emailAddress"
          type="email"
          name="emailAddress"
          placeholder="Email Address"
          value={emailAddress}
          onChange={e => setEmailAddress(e.target.value)}
        />
        </div>
        <label htmlFor='address'>Address</label>
        <input
          id="address"
          type="text"
          name="address"
          placeholder="Address"
          value={address} 
          onChange={e => setAddress(e.target.value)}
          style={{marginRight:"20px"}}
        />
                <label htmlFor='hospitalLicense'>Hospital License</label>
        <input
          id="hospitalLicense"
          type="text"
          name="hospitalLicense"
          placeholder="Hospital License"
          value={hospitalLicense} 
          onChange={e => setHospitalLicense(e.target.value)}
          style={{marginRight:"20px"}}
        />
                <label htmlFor='hook_url'>Hook URL</label>
        <input
          id="hook_url"
          type="text"
          name="hook_url"
          placeholder="Hook URL"
          value={hook_url} 
          onChange={e => setHook_url(e.target.value)}
          style={{marginRight:"20px"}}
        />
        {isOtpRecieved && (
                <div style={{display:"flex", justifyContent:"space-between"}}>
        <input
          id="phoneString"
          type="number"
          name="phoneString"
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
    </div>
  );
};

export default HospitalRegister;

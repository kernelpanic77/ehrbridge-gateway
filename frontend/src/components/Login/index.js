import React, { useState } from 'react';
import Swal from 'sweetalert2';
import axios from 'axios';
const Login = ({ setIsAuthenticated, setIsRegistering }) => {
  // const adminEmail = 'admin@example.com';
  // const adminPassword = 'qwerty';

  const [abhaID, setAbhaID] = useState('');
  const [phoneNum, setPhoneNum] = useState('');

  const handleLogin = e => {
    e.preventDefault();
    let details = {
      abhaID : abhaID? abhaID : null,
      phoneNum : phoneNum? phoneNum : null
    }
    // send a post request with details to http://localhost:8090/author/login
    axios.post("http://localhost:8080/patient/login",details).then((response) => {
      console.log(response);
      if (response.data != null) {
        Swal.fire({
          timer: 1500,
          showConfirmButton: false,
          willOpen: () => {
            Swal.showLoading();
          },
          willClose: () => {
            localStorage.setItem('is_authenticated', true);
            localStorage.setItem('author_id', 1);
            setIsAuthenticated(true);
  
            Swal.fire({
              icon: 'success',
              title: 'Successfully logged in!',
              showConfirmButton: false,
              timer: 1500,
            });
          },
        });
      } 
      else {
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
              text: 'Incorrect email or password.',
              showConfirmButton: true,
            });
          },
        });
      }
    });

  };

  return (
    <div className="small-container">
      <form onSubmit={handleLogin} style={{textAlign:"center"}}>
        <h1>Patient Login</h1>
        <label htmlFor="abhaID">ABHA ID</label>
        <input
          id="abhaID"
          type="text"
          name="abhaID"
          placeholder="Abha ID"
          value={abhaID}
          onChange={e => setAbhaID(e.target.value)}
        />
        <h2 style={{margin:0}}>(or)</h2>
        <label htmlFor="phoneNum">Phone Number</label>
        <input
          id="phoneNum"
          type="tel"
          name="phoneNum"
          placeholder="Phone Number"
          value={phoneNum}
          onChange={e => setPhoneNum(e.target.value)}
        />
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

import React, {useState, useEffect} from 'react';

import Logout from '../Logout';

const Header = ({ setIsAdding, setIsAuthenticated, setIsGeneratingPaper }) => {
  const [abhaID, setAbhaID] = useState("p1");

  useEffect(() => {
    setAbhaID(JSON.parse(localStorage.getItem('abha_id')));
  }, []);
  
  return (
    <header>
      <h1>Patient Dashboard</h1>
      <div style={{ marginTop: '30px', marginBottom: '18px', display:"flex", justifyContent:"space-between" }}>
        <button onClick={() => setIsAdding(true)}>Add Hospital Visit</button>
        {/* <button style ={{marginLeft:'10px'}} onClick={() => setIsGeneratingPaper(true)}>Generate Consent</button> */}
        <button onClick={() => setIsAdding(true)}>View Health Records</button>
        <button onClick={() => setIsAdding(true)}>View Consent Requests</button>
        <button className="muted-button" disabled>ABHA ID: {abhaID}</button>
        <Logout setIsAuthenticated={setIsAuthenticated} />
      </div>
    </header>
  );
};

export default Header;

import React from "react";
import "./App.css";
import ProfileCard from "./ProfileCard";

function App() {
  const [profiles, setProfiles] = React.useState([]);

  React.useEffect(() => {
    fetch("/api/profiles")
      .then(res => res.json())
      .then(data => setProfiles(data))
      .catch(err => console.error("API error:", err));
  }, []);

  return (
    <div style={{ padding: "2rem", fontFamily: "Arial" }}>
      <h1>Team Profiles</h1>
      <div style={{ display: "flex", gap: "2rem", flexWrap: "wrap" }}>
        {profiles.map((p, i) => (
          <ProfileCard key={i} profile={p} />
        ))}
      </div>
    </div>
  );

}

export default App;

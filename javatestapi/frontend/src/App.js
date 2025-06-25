import React from "react";
import "./App.css";
import ProfileCard from "./ProfileCard";

function App() {
  const [profiles, setProfiles] = React.useState([]);
  // const [aboutMe, setAboutMe] = React.useState(null);

  React.useEffect(() => {
    // Fetch team profiles from REST
    fetch("/api/profiles")
      .then(res => res.json())
      .then(data => setProfiles(data))
      .catch(err => console.error("API error (profiles):", err));

    // // Fetch "About Me" profile from GraphQL
    // fetch("http://localhost:4000/graphql", {
    //   method: "POST",
    //   headers: { "Content-Type": "application/json" },
    //   body: JSON.stringify({ query: `query { aboutMe(userId: 1) { id bio } }` })
    // })
    //   .then(res => res.json())
    //   .then(result => setAboutMe(result.data.aboutMe))
    //   .catch(err => console.error("GraphQL error (aboutMe):", err));
  }, []);

  return (
    <div style={{ padding: "2rem", fontFamily: "Arial" }}>
      <h1>Team Profiles</h1>

      <div style={{ display: "flex", gap: "2rem", flexWrap: "wrap", marginTop: "2rem" }}>
        {profiles.map((p, i) => (
          <ProfileCard key={i} profile={p} />
        ))}
      </div>

      {/* {aboutMe && (
        <div>
          <h2>About Me</h2>
          <p><strong>ID:</strong> {aboutMe.id}</p>
          <p><strong>Bio:</strong> {aboutMe.bio}</p>
        </div>
      )} */}

    </div>
  );
}

export default App;

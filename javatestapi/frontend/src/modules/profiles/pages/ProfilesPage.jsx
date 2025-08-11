import { useEffect, useState } from "react";
import { fetchProfiles } from "../api";
import ProfileCard from "../components/ProfileCard";
import "./ProfilesPage.css";

export default function ProfilesPage() {
  const [profiles, setProfiles] = useState([]);
  const [error, setError] = useState(null);
  useEffect(() => {
    let cancelled = false;
    fetchProfiles()
      .then(data => { if (!cancelled) setProfiles(data); })
      .catch(e => { if (!cancelled) setError(e?.message || "Error"); });
    return () => { cancelled = true; };
  }, []);
  if (error) return <div className="error">API error (profiles): {error}</div>;

  return (
    <div style={{ padding: "2rem", fontFamily: "Arial" }}>
      <h1>Team Profiles</h1>
      <div style={{ display: "flex", gap: "2rem", flexWrap: "wrap", marginTop: "2rem" }}>
        {profiles.map((p, i) => (
          <ProfileCard key={i} profile={p} />

          // <div key={i} className="card">
          //   <div className="card-title">{p.name}</div>
          //   <div className="card-subtitle">{p.title}</div>
          // </div>
        ))}
        {!profiles.length && <div>No profiles yet.</div>}
      </div>
    </div>
  );
}

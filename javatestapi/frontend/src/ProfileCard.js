function ProfileCard({ profile }) {
  return (
    <div
      style={{
        border: "1px solid #ccc",
        borderRadius: "8px",
        padding: "1rem",
        width: "250px",
        boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
      }}
    >
      <img
        src={profile.image}
        alt={profile.name}
        style={{ width: "100px", height: "100px", borderRadius: "50%" }}
      />
      <h2>{profile.name}</h2>
      <h4>{profile.title}</h4>
      <p style={{ margin: 0, fontWeight: "bold" }}>{profile.location}</p>
      <p>{profile.summary}</p>
    </div>
  );
}

export default ProfileCard;

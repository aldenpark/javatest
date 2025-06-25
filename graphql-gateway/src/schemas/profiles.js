export default `
  type Profile {
    id: String!
    full_name: String!
    title: String
    location: String
    summary: String
    image: String
    active: Boolean
  }

  extend type Query {
    profiles: [Profile]
    profile(id: String!): Profile
  }
`;

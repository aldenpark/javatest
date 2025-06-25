export default `
  type AboutMe {
    id: Int!
    bio: String
  }

  type Query {
    aboutMe(userId: Int!): AboutMe
  }
`;

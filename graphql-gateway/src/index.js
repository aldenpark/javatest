// npm start

import { ApolloServer } from '@apollo/server';
import { expressMiddleware } from '@apollo/server/express4';
import { makeExecutableSchema } from '@graphql-tools/schema';
import { json } from 'express';
import express from 'express';
import cors from 'cors';
import bodyParser from 'body-parser';

// Import typeDefs and resolvers
import aboutMeTypeDefs from './schemas/aboutMe.js';
import profilesTypeDefs from './schemas/profiles.js';
import aboutMeResolvers from './resolvers/aboutMe.js';
import profilesResolvers from './resolvers/profiles.js';

const typeDefs = [aboutMeTypeDefs, profilesTypeDefs];
const resolvers = [aboutMeResolvers, profilesResolvers];

const schema = makeExecutableSchema({ typeDefs, resolvers });

const app = express();
const server = new ApolloServer({ schema });

const PORT = process.env.PORT || 4000;

(async () => {
  await server.start();
  app.use('/graphql', cors(), json(), expressMiddleware(server));
  app.listen(PORT, () => console.log(`🚀 GraphQL server ready at http://localhost:${PORT}/graphql`));
})();

import dotenv from 'dotenv';
dotenv.config();

import pg from 'pg';

const { Pool } = pg;

const pool = new Pool({
  host: process.env.PG_HOST,
  user: process.env.PG_USER,
  password: process.env.PG_PASSWORD,
  database: process.env.PG_DATABASE,
  port: process.env.PG_PORT || 5432,
  ssl: {
    require: true,
    rejectUnauthorized: false, // Needed for RDS unless you provide a cert
  },
});

export const query = (text, params) => pool.query(text, params);

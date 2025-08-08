-- Countries, Cities, Campuses
INSERT INTO country(id, name) VALUES (1, 'Wonderland') ON CONFLICT DO NOTHING;
INSERT INTO city(id, name, country_id) VALUES (1, 'Heart City', 1) ON CONFLICT DO NOTHING;
INSERT INTO campus(id, name, city_id) VALUES (1, 'Main Campus', 1) ON CONFLICT DO NOTHING;

-- SUPER_ADMIN user (username: superadmin, password: admin123)
-- Password will be updated by ApplicationRunner if not encoded
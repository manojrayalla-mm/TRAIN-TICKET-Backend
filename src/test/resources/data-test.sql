-- Test Seed Data
INSERT INTO users (username, email, password, role) VALUES ('admin', 'admin.propertypro.com', 'admin123', 'ADMIN');
INSERT INTO users (username, email, password, role) VALUES ('manager', 'manager.propertypro.com', 'manager123', 'PROPERTY_MANAGER');
INSERT INTO users (username, email, password, role) VALUES ('tech_alex', 'alex.plumber.propertypro.com', 'tech123', 'TECHNICIAN');
INSERT INTO users (username, email, password, role) VALUES ('tech_sarah', 'sarah.electric.propertypro.com', 'tech123', 'TECHNICIAN');
INSERT INTO users (username, email, password, role) VALUES ('tech_mike', 'mike.hvac.propertypro.com', 'tech123', 'TECHNICIAN');
INSERT INTO users (username, email, password, role) VALUES ('tenant_john', 'john.doe.gmail.com', 'tenant123', 'TENANT');
INSERT INTO users (username, email, password, role) VALUES ('tenant_emily', 'emily.watson.gmail.com', 'tenant123', 'TENANT');

INSERT INTO properties (property_code, title, address, city, state, zip_code, property_type, status, rent_amount, bedrooms, bathrooms, description)
VALUES ('PROP-101', 'Sunset Heights Luxury 2BHK', '101 Ocean Drive', 'Miami', 'FL', '33139', 'APARTMENT', 'OCCUPIED', 2400.00, 2, 2, 'Luxury oceanfront condo with balcony');

INSERT INTO properties (property_code, title, address, city, state, zip_code, property_type, status, rent_amount, bedrooms, bathrooms, description)
VALUES ('PROP-102', 'Oakwood Suburban Villa', '452 Elm Street', 'Austin', 'TX', '78701', 'HOUSE', 'OCCUPIED', 3200.00, 3, 2, 'Spacious detached family home with private yard');

INSERT INTO properties (property_code, title, address, city, state, zip_code, property_type, status, rent_amount, bedrooms, bathrooms, description)
VALUES ('PROP-103', 'Downtown Executive Studio', '789 Broadway Ave', 'New York', 'NY', '10003', 'STUDIO', 'AVAILABLE', 1950.00, 1, 1, 'Compact modern studio in city center');

INSERT INTO technicians (user_id, first_name, last_name, email, phone, specialization, is_available, hourly_rate, status)
VALUES (3, 'Alex', 'Rivera', 'alex.plumber.propertypro.com', '555-0101', 'PLUMBING', TRUE, 65.00, 'ACTIVE');

INSERT INTO technicians (user_id, first_name, last_name, email, phone, specialization, is_available, hourly_rate, status)
VALUES (4, 'Sarah', 'Jenkins', 'sarah.electric.propertypro.com', '555-0102', 'ELECTRICAL', TRUE, 75.00, 'ACTIVE');

INSERT INTO technicians (user_id, first_name, last_name, email, phone, specialization, is_available, hourly_rate, status)
VALUES (5, 'Mike', 'Chang', 'mike.hvac.propertypro.com', '555-0103', 'HVAC', TRUE, 80.00, 'ACTIVE');

INSERT INTO tenants (user_id, first_name, last_name, email, phone, emergency_contact, property_id, lease_start_date, lease_end_date, status)
VALUES (6, 'John', 'Doe', 'john.doe.gmail.com', '555-0201', 'Jane Doe (555-0202)', 1, '2026-01-01', '2026-12-31', 'ACTIVE');

INSERT INTO tenants (user_id, first_name, last_name, email, phone, emergency_contact, property_id, lease_start_date, lease_end_date, status)
VALUES (7, 'Emily', 'Watson', 'emily.watson.gmail.com', '555-0203', 'Robert Watson (555-0204)', 2, '2026-03-01', '2027-02-28', 'ACTIVE');

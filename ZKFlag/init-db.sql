\i schema.sql;

INSERT INTO roles (id, description, role_type) VALUES
(1, 'System Administrator', 'SystemAdmin'),
(2, 'Project Administrator', 'ProjectAdmin'),
(3, 'Instance Administrator', 'InstanceAdmin')
ON CONFLICT (id) DO NOTHING;


INSERT INTO users (email, name, password, role_id) 
VALUES 
('admin@example.com', 'System Admin', '$2a$10$C3cKEza2D1Kbff/Evc8SCe03X4sp6LPBeJmi4NQpVMi0Zso0R0Gl6', 1)
ON CONFLICT (email) DO NOTHING;
# Company User Management with Vaadin

## User Capabilities
### Super Admin
- Access any record available in the database, without being tied to any Company or Department
- Store any data (Holding Company, Child Company, Department, User)
- Edit any data
- Delete any record, while still following delete validation rules
### Company Admin
- Access any record (Company, Department, User) that are tied to corresponding Admin's Holding Company
- Add new Child Company to the corresponding Holding Company
- Add new Department to the corresponding Holding Company or any Child Company inside the corresponding Holding Company
- Register new User to the corresponding Holding Company or any Child Company inside the corresponding Holding Company
- Edit any record mentioned before
- Delete any record mentioned before
### Department Admin
- Access any User that are tied to corresponding Admin's Department
- Register new User to the corresponding Department
- Edit any record mentioned before
- Delete any record mentioned before


Capabilities for User Admin and User will be added soon
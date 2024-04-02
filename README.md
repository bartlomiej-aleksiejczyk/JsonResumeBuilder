# Resume Builder Application

This application generates resumes from JSON data and Jinja2-to-LaTeX templates, allowing for dynamic creation of professional-looking resumes.

## How to Run It Locally

### Prerequisites

- Docker and Docker Compose must be installed on your machine. Installation guides for Docker can be found [here](https://docs.docker.com/get-docker/) and for Docker Compose [here](https://docs.docker.com/compose/install/).

### Setup

1. Clone the repository to your local machine:

```bash
git clone https://your-repository-url.git
cd your-repository-directory
```

### Running the Application

To start the Resume Builder application using Docker Compose, follow these steps:

1. **Open a Terminal**: Navigate to the root directory of the project where your `docker-compose-dev.yml` file is located.

2. **Start the Services**: Execute the following command to start all the services defined in the `docker-compose-dev.yml` file. This includes your application, as well as any databases or message brokers you've defined as services:

   ```bash
   docker-compose -f docker-compose-dev.yml up --build
   ```

3. **Access the Application**: Once the containers are up and running, you can access the Resume Builder application by opening your web browser and navigating to:

   ```
   http://localhost:8080/springboot-resume-builder
   ```

By following these steps, you'll be able to run and interact with the Resume Builder application locally using Docker Compose. For further customization or development, refer to the specific service configurations within your `docker-compose-dev.yml` file.

## License

This project is licensed under the MIT License.

Below is the content of `SampleData.md` that you can use for your Resume Builder Application. This markdown file contains both JSON data and a LaTeX template that can be used to generate a PDF document.

## JSON Data

```json
{
  "name": "Jane Doe",
  "date": "April 2024",
  "email": "janedoe@example.com",
  "phone": "+1234567890",
  "educations": [
    {
      "degree": "B.Sc. in Computer Science",
      "institution": "University of Somewhere",
      "year": "2022",
      "major": "Computer Science",
      "gpa": "3.8/4.0"
    }
  ],
  "jobs": [
    {
      "position": "Software Developer",
      "company": "Tech Innovations Inc.",
      "location": "New York",
      "duration": "2021 - Present",
      "duties": [
        "Developed and maintained code for in-house software solutions",
        "Collaborated with a team to provide IT support"
      ]
    },
    {
      "position": "Intern",
      "company": "Startup Hub",
      "location": "San Francisco",
      "duration": "2020 - 2021",
      "duties": [
        "Assisted in the development of apps",
        "Performed routine system maintenance"
      ]
    }
  ],
  "skills": [
    {
      "name": "Programming Languages",
      "level": "Python, Java, C++"
    },
    {
      "name": "Frameworks",
      "level": "Django, React"
    },
    {
      "name": "Databases",
      "level": "MySQL, PostgreSQL"
    }
  ]
}
```

## LATEX-to-Jinja2 Template

```latex
\documentclass{article}
\usepackage[utf8]{inputenc}
\usepackage{enumitem}

\begin{document}

\title{Curriculum Vitae}
\author{\VAR{name}}
\date{\VAR{date}}
\maketitle

\section*{Personal Information}
\begin{itemize}
  \item Email: \VAR{email}
  \item Phone: \VAR{phone}
\end{itemize}

\section*{Education}
\BLOCK{for education in educations}
  \subsection*{\VAR{education.degree}}
  \textbf{\VAR{education.institution}} \hfill \VAR{education.year}
  \begin{itemize}
    \item Major: \VAR{education.major}
    \item GPA: \VAR{education.gpa}
  \end{itemize}
\BLOCK{endfor}

\section*{Work Experience}
\BLOCK{for job in jobs}
  \subsection*{\VAR{job.position}}
  \textbf{\VAR{job.company}}, \VAR{job.location} \hfill \VAR{job.duration}
  \begin{itemize}
    \BLOCK{for duty in job.duties}
      \item \VAR{duty}
    \BLOCK{endfor}
  \end{itemize}
\BLOCK{endfor}

\section*{Skills}
\begin{itemize}[label={}]
  \BLOCK{for skill in skills}
    \item \VAR{skill.name}: \VAR{skill.level}
  \BLOCK{endfor}
\end{itemize}

\end{document}
```

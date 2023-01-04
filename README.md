# Sveletor: Svelte + Ktor

Sveletor is a starter project for SvelteKit multi-page applications that are served via a Ktor Backend.

Sveletor is in V 0.0.1, as in it barely works without some duct tape, but it is worth it to be able to use an actually good UI framework w/Ktor.

## Setup

The application is already configured, just clone this repository and the gradle scripts should be able to run and configure the necessary packages itself.

If you'd like to clone a blank version of this project, with just the home page and the widgets, clone this branch:

build.gradle.kts relies on npmBuild.cmd which is literally just: 

```cmd
cd .\src\web\
call npm i
call npm run build
```

So when you rerun the server, the frontend is also recompiled. 

## Structure

src/jvmMain is source code for the Ktor server, where com.sveletor.application.Server.kt is the entrypoint for the application.

src/web contains the svelte app. When the application is built, the static adapter writes the contents of the Svelte App's build output to /src/jvmMain/resources/web.

## Routing

Use the prebuilt `sveltePage` application extension function to serve a static svelte page. Supplying the endpoint will tell also automatically find the file in the server's resource folder.

Because Svelte's Static Router is set up for file based routing, you should adopt the same file pattern for your endpoints.

For Example:

If your svelte application's routes directory looks like this:

```
routes
|_ +page.svelte
|_ foo
   |_ +page.svelte
   |_ bar
      |_ +page.svelte
```

You'd serve those three pages like so:

```
sveltePage("/")

sveltePage("/foo")

sveltePage("/foo/bar")
```

And the function would automatically associate the endpoints to the routes:

```
"/"         becomes index.html
"/foo"      becomes foo.html
"/foo/bar"  becomes foo/bar.html
```

### Authentication

This is family simple in Ktor, for any page you'd like to lock behind a login, simply replace a call to `sveltePage` with a call to `authenticatedSveltePage`

This will preform a validation check on the `SveletorSession` object associated w/ the application call, which, if invalid, will redirect a user to a login screen.

In the case that you'd like to preform further validation before a page is served to a client, there is a `preResponse` parameter in the `sveltePage` function which allows you to write a function to preform additional validation and then potentially redirect if some conditions aren't met

### Client/Server communication

- Svelte's load functions aren't accesible in the static adapter. 
  - Instead, if you need data to populate on page loads, you have to fetch from the Ktor API using fetch in Svelte's `onMount` lifecycle hook.
  - You can see a simple example of this working in the application where the Svelte API fetch's session data. Speaking of which:
- Getting cookies and session data can be annoying, but I built in a method for retrieving session data from the server. As such the `/session` route is reserved for this.
  - This route returns the SveletorSession associated w/ the call as JSON data.
- There isn't any code sharing, but serializing the data as JSON when sending to the client and deserializing it on the way in makes this almost a non issue.
  - In the future, I may choose to write some scripts that generate data classes in jvmCommon into TS interfaces, but that is a later issue.
- You are still entirely able to build HTML pages using Ktor's HTML DSL as well in case you don't really need a full Svelte page for them.
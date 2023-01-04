<script lang="ts">
    import { Link } from '$lib';
    import { onMount } from 'svelte';
    import { getCookie, getSessionData } from '$lib/functions'

    let name = 'User';
    onMount(async () => {
        const session = await getSessionData();
        console.log(session);

        if (session.username !== null && session.username !== undefined && session.username !== '')
            name = session.username;
    });
</script>

<h1>Hello {name}, welcome to Sveletor!</h1>

<p>As you can see, this is another Svelte Static page, but now you are logged in as: {name}</p>

<p>Click <Link link='/logout'>here</Link> to log out.</p>
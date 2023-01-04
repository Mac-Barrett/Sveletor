<script lang="ts">
    import { Link } from '$lib';
    let name = '';
</script>

<h1>Hello {name}, welcome to Sveletor!</h1>

<p>As you can see, this is another Svelte Static page, but now you are logged in as : {name}</p>

<p>Click <Link link='/logout'>here</Link> to log out.</p>
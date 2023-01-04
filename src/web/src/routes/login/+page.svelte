<script lang="ts">
    import { Link } from '$lib';

    let form: HTMLFormElement|null = null;

    let msg = '';
    async function login() {
        //@ts-ignore
        const data = new URLSearchParams(new FormData(form));
        if (form !== null && form !== undefined) {
            const response = await fetch(`/login`, {
                method: 'POST',
                body: data
            });

            msg = await response.text();
        }
    }
</script>

<h1>Login</h1>
<form bind:this={form}>
    <input type="text" name="username" placeholder="Enter a string">
    <input type="button" value="Login" on:click={login}>
</form>
<br>
{#if msg !== ''}
<p>{msg} <Link link={window.location.href}>Reload the page</Link></p>
{/if}
<Link link='/'>Back to home page...</Link>

<style>

</style>
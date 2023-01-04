export function getCookie(name: string) {
    let cookieArray = document.cookie.split(";");

    console.log(`Cookies: ${document.cookie}`)

    for (let i = 0; i < cookieArray.length; i++) {
        let pair = cookieArray[i].split("=");

        if (pair.length >= 2) {
            // In case there was a '=' in the Base64 String.
            for (let i = 2; i < pair.length; i++) {
                pair[1] += `=${pair}`;
            }

            // Decode String and return it.
            if (name === pair[0].trim()) {
                return btoa(pair[1].trim());
            }
        }
    }
    return '';
}

export async function getSessionData(): Promise<any> {
    const response = await fetch('/session', { method: 'GET' });
    
    return await response.json();
}
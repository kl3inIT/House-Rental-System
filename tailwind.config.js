module.exports = {
  content: [
    './src/main/resources/templates/**/*.html', // Thymeleaf HTML
    './src/main/resources/static/js/**/*.js'    // Nếu có JS kèm class
  ],
  theme: {
    extend: {},
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('@tailwindcss/typography'),
    require('@tailwindcss/aspect-ratio'),
    require('@tailwindcss/line-clamp')
  ],
}
